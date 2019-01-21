package me.javaee.meetup.utils;

import me.javaee.meetup.Meetup;
import me.javaee.meetup.handlers.Scenario;

import java.util.List;
import java.util.Random;

public class RandomSelector {
    List<Scenario> items = Meetup.getPlugin().getScenarios();
    Random rand = new Random();
    int totalSum = 100;

    public RandomSelector() {
        /*for (Scenario item : items) {
            //totalSum = totalSum + item.getPercentage();
        }*/
    }

    /*public Scenario getRandom() {
        int index = rand.nextInt(totalSum);
        int sum = 0;
        int i = 0;
        while (/*sum < (index))i < 6) {
            if (items.get(i++) != null) {
                sum = sum + items.get(i++).getPercentage();
            }
        }
        return items.get(Math.max(0, i - 1));
    }*/
}