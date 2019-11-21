package com.deep.customer;

class User {
    public final String email,f_name,l_name,phone,aadhar;

    public User(String email,String f_name, String l_name, String phone, String aadhar ) {
        this.f_name = f_name;
        this.email = email;
        this.l_name = l_name;
        this.phone = phone;
        this.aadhar = aadhar;
    }
}
