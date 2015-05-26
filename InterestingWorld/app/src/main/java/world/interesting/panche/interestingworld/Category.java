package world.interesting.panche.interestingworld;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panche on 31/03/2015.
 */
public class Category {
    List<String> list=new ArrayList<>();

    public Category()
    {
        list.add("Monumento");
        list.add("Museo");
        list.add("Bar");
        list.add("Restaurante");
        list.add("Playa");
        list.add("Mirador");
        list.add("Ocio");
    }

    public List<String> GetList()
    {
        return list;
    }
    public int GetIdCategory(String selected)
    {
        int id=0;

        if(selected.equals("Monumento"))
        {
           id=1;
        }
        if(selected.equals("Museo"))
        {
            id=2;
        }
        if(selected.equals("Playa"))
        {
            id=3;
        }
        if(selected.equals("Bar"))
        {
            id=4;
        }
        if(selected.equals("Restaurante"))
        {
            id=5;
        }
        if(selected.equals("Mirador"))
        {
            id=6;
        }
        if(selected.equals("Ocio"))
        {
            id=7;
        }
        return id;
    }
}
