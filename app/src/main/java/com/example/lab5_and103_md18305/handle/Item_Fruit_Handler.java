package com.example.lab5_and103_md18305.handle;


import com.example.lab5_and103_md18305.model.Fruit;
import com.google.gson.annotations.SerializedName;

public interface Item_Fruit_Handler  {
    public void Delete(String id);

    public void Update(String id, Fruit fruit);

    public void Dentail(String id);
}
