package com.starfinanz.LibraryBeanWithSpringBoot;

import com.opencsv.*;
import com.opencsv.bean.CsvBindByName;

public class BookBean {

    @CsvBindByName
    private long isbn;

    @CsvBindByName
    private String titel;

    @CsvBindByName
    private String autor;

    @CsvBindByName
    private String status;

    @CsvBindByName
    private String besitzer;

    public BookBean() {
    }

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBesitzer() {
        return besitzer;
    }

    public void setBesitzer(String besitzer) {
        this.besitzer = besitzer;
    }
}
