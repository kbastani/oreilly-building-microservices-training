package com.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

public class Spring101Application {

	public static void main(String[] args) {

		AnnotationConfigApplicationContext ac =
				new AnnotationConfigApplicationContext(JavaConfiguration.class);

		// -Dspring.profiles.active=cloud,embedded

		ac.start();
		ac.registerShutdownHook();

		BarService barService = new BarService();
		FooService fooService = new FooService(barService);

		ac.registerBean(BarService.class, () -> barService);
		ac.registerBean(FooService.class, () -> fooService);

		Assert.notNull(ac.getBean(FooService.class), "fooService should not be null!");
		Assert.notNull(ac.getBean(BarService.class), "barService should not be null!");

		ac.stop();
	}
}

@Configuration
@ComponentScan
class JavaConfiguration {

}

@Component
class BarService {

	public void hi() {
	}
}

@Component
class FooService {


	private final BarService barService;

	public FooService(BarService barService) {
		this.barService = barService;
		this.barService.hi();
	}


}
