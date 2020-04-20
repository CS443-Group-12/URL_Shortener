package group12.rest.url;

import java.util.Random;

public class UniqueLinkGenerator {
    private String link;
    private String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private Random rand;

    public UniqueLinkGenerator() {
        this.link = "";
        this.rand = new Random();
    }

    public String generate() {
        for (int i = 0; i < 7; i++)
            link += alphabet.charAt(rand.nextInt(62));
        
        return link;
    }
}