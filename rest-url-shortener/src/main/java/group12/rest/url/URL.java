package group12.rest.url;

import java.util.Date;

public class URL {
    public String original;
    public String shortened;
    public String custom;
    public Date expiry;

    @Override
    public String toString() {
        return this.original + "\n" + this.shortened + "\n" + this.custom + "\n" + this.expiry.toString();
    }
}