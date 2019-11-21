package com.deep.owner;

class User {
    public String email,f_name,l_name,phone;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public User(String email, String f_name, String l_name, String phone ) {
        this.f_name = f_name;
        this.email = email;
        this.l_name = l_name;
        this.phone = phone;
    }
}
