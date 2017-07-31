package com.codepath.nytimessearch.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by STEPHAN987 on 7/30/2017.
 */

public class SearchFilter implements Serializable {
    public String getBeginDate() {
        return beginDate;
    }

    public String getSortOrder() {
        return sortOrder.replace("-------","");
    }

    public ArrayList<String> getNewsDeskValues() {
        return newsDeskValues;
    }

    public String getNewsDeskValuesSingleString() {
        String val = "";
        if (newsDeskValues.size() > 0)
        {
            for (int i = 0; i < newsDeskValues.size(); i++){
                if (val.trim() == "")
                    val = "\"" + newsDeskValues.get(i) + "\"";
                else
                    val = val + " \"" + newsDeskValues.get(i) + "\"";
            }

            val = "news_desk:(" + val + ")";
        }
        return val;
    }

    String beginDate;
    String sortOrder;
    ArrayList<String> newsDeskValues;

    public SearchFilter(String beginDate, String sortOrder, ArrayList<String> newsDeskValues){
        this.beginDate = beginDate;
        this.sortOrder = sortOrder;
        this.newsDeskValues = newsDeskValues;
    }
}
