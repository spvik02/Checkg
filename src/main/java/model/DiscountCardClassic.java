package model;

import java.time.LocalDate;
import java.util.Objects;

public class DiscountCardClassic implements DiscountCard {
    private int id;
    private double percentageOfDiscount;
    private LocalDate dateOfRegistration;

    public DiscountCardClassic(int id, double percentageOfDiscount, LocalDate date) {
        this.id = id;
        this.percentageOfDiscount = percentageOfDiscount;
        this.dateOfRegistration = date;
    }

    public int getId() {
        return id;
    }

    public double getPercentageOfDiscount() {
        return percentageOfDiscount;
    }

    public void setPercentageOfDiscount(double percentageOfDiscount) {
        this.percentageOfDiscount = percentageOfDiscount;
    }

    public void setId(int id) {
        this.id = id;
    }



    public LocalDate getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(LocalDate dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    @Override
    public String toString() {
        return "DiscountCard{" +
                "id=" + id +
                ", percentageOfDiscount=" + percentageOfDiscount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscountCardClassic that = (DiscountCardClassic) o;
        return id == that.id && Double.compare(that.percentageOfDiscount, percentageOfDiscount) == 0 && dateOfRegistration.equals(that.dateOfRegistration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, percentageOfDiscount, dateOfRegistration);
    }
}
