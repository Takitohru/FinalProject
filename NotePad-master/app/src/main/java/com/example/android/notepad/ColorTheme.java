package com.example.android.notepad;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ColorTheme {
    AppCompatActivity ap;
    public ColorTheme(AppCompatActivity _ap){ap=_ap;}
    public void updateTheme(int _data){
        String data=Integer.toString(_data);
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try{
            out=ap.openFileOutput("data", Context.MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if(writer!=null){
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void loadTheme(){
        FileInputStream in=null;
        BufferedReader reader= null;
        StringBuilder content=new StringBuilder();
        try{
            in=ap.openFileInput("data");
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while((line=reader.readLine())!=null){
                content.append(line);
            }
            ap.setTheme(Integer.parseInt(content.toString()));
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try{
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
