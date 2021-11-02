package io.cstool.comply22;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableNeo4jAuditing
public class Comply22Application {

	public static void main(String[] args) {
		SpringApplication.run(Comply22Application.class, args);
	}



}
