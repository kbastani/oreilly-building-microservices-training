package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.JobExecutionEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@EnableBatchProcessing
@SpringBootApplication
public class BatchApplication {

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Person {

		private String email, firstName;
		private int age;
		private Long id;
	}

	@Component
	public static class BatchListener {

		private Log log = LogFactory.getLog(getClass());

		private final JdbcTemplate jdbcTemplate;

		public BatchListener(DataSource dataSource) {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}

		@EventListener(JobExecutionEvent.class)
		public void onBatchEvent(JobExecutionEvent e) {
			this.log.info("batch event: " + e.getJobExecution().getExitStatus());
			RowMapper<Person> rowMapper = (resultSet, i) -> new Person(resultSet.getString("email"),
					resultSet.getString("first_name"), resultSet.getInt("age"),
					resultSet.getLong("age"));
			List<Person> personList = this.jdbcTemplate.query("select * from PEOPLE", rowMapper);
			personList.forEach(System.out::println);
		}
	}

	@Configuration
	public static class Step1 {

		@Bean
		ItemReader<Person> fileReader(@Value("file:///Users/jlong/Desktop/in.csv") Resource resource) throws Exception {
			return new FlatFileItemReaderBuilder<Person>()
					.name("file-reader")
					.resource(resource)
					.targetType(Person.class)
					.delimited().delimiter(",").names(new String[]{"firstName", "age", "email"})
					.build();
		}

		@Bean
		JdbcBatchItemWriter<Person> jdbcWriter(DataSource ds) {
			return new JdbcBatchItemWriterBuilder<Person>()
					.dataSource(ds)
					.sql("insert into PEOPLE( AGE, FIRST_NAME, EMAIL) values (:age, :firstName, :email)")
					.beanMapped()
					.build();
		}
	}

	@Configuration
	public static class JobConfiguration {

		@Bean
		Job job(JobBuilderFactory jbf, StepBuilderFactory sbf, Step1 step1) throws Throwable {

			Step s1 = sbf.get("s1")
					.<Person, Person>chunk(100)
					.writer(step1.jdbcWriter(null))
					.reader(step1.fileReader(null))
					.build();

			return jbf
					.get("j1")
					.incrementer(new RunIdIncrementer())
					.start(s1)
					.build();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}
}
