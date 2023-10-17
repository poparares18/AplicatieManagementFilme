package com.example.aplicatiemanagementfilme.asyncTask;

public interface Callback<R> {
    void runResultOnUiThread(R result);
}
