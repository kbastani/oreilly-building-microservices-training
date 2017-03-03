
package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

@EnableBatchProcessing
@SpringBootApplication
public class BatchApplication {

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Person {

		private Long id;
		private String firstName, email;
		private int age;
	}

	@Configuration
	public static class Step1Configuration {

		@Bean
		ItemReader<Person> fileReader(@Value("file://${input-file}") Resource resource) throws Exception {
			return new FlatFileItemReaderBuilder<Person>()
					.resource(resource)
					.targetType(Person.class)
					.delimited().delimiter(",").names(new String[]{"firstName", "age", "email"})
					.name("file-reader")
					.build();
		}

		@Bean
		ItemWriter<Person> jdbcWriter(DataSource dataSource) {
			return new JdbcBatchItemWriterBuilder<Person>()
					.sql("insert into PEOPLE( first_name, age, email) values ( :firstName, :age, :email) ")
					.dataSource(dataSource)
					.beanMapped()
					.build();
		}
	}


	@Bean
	Job job(JobBuilderFactory jbf,
	        StepBuilderFactory sbf,
	        Step1Configuration step1) throws Throwable {

		Step s1 = sbf.get("file-db")
				.<Person, Person>chunk(1000)
				.reader(step1.fileReader(null))
				.writer(step1.jdbcWriter(null))
				.build();

		return jbf.get("etl")
				.start(s1)
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}
}