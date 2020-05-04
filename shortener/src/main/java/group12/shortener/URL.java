package group12.shortener;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class URL {
    @Id
    Long id;
    
    public String original;
    public String shortened;
    public String custom;
    public Date expiry;

    @Override
    public String toString() {
        return this.original + "\n" + this.shortened + "\n" + this.custom + "\n" + this.expiry.toString();
    }
}