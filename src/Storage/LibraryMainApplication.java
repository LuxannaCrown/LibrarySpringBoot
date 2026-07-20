package com.starfinanz.LibraryBeanWithSpringBoot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryMainApplication implements CommandLineRunner {

	void main() {
		SpringApplication.run(LibraryMainApplication.class);
	}

    @Override
    public void run(String... args)  {
        Library library = new Library();
        library.starteLibrary();
    }
}
