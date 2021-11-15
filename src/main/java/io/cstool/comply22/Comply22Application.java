package io.cstool.comply22;

import io.cstool.comply22.service.TimelineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableNeo4jAuditing
@Slf4j
public class Comply22Application {

	public static void main(String[] args) {
		SpringApplication.run(Comply22Application.class, args);
	}

	@Autowired
	private TimelineService timelineService;

	@PostConstruct
	void init() {
		log.info("Initializing database...");
		timelineService.initialize();
	}

}
