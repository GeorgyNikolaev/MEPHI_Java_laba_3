package domain;

import enums.SorcererRank;

public class Sorcerer {
    String name;
    SorcererRank rank;

    public Sorcerer(String name, SorcererRank rank) {
        this.name = name;
        this.rank = rank;
    }

    public Sorcerer() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SorcererRank getRank() {
        return rank;
    }

    public void setRank(SorcererRank rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "Sorcerer{" +
                "name='" + name + '\'' +
                ", rank=" + rank +
                '}';
    }
}