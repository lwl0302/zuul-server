package cn.mrray.entity.domain;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "t_user")
public class User extends SuperEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(targetEntity=Token.class,mappedBy = "user")
    private List<Token> TokenList;

    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Token> getTokenList() {
        return TokenList;
    }

    public void setTokenList(List<Token> tokenList) {
        TokenList = tokenList;
    }
}
