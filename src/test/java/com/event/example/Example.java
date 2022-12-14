package com.event.example;

import org.nishat.util.event.Event;
import org.nishat.util.event.EventGroup;
import org.nishat.util.event.EventManager;
import org.nishat.util.event.Listener;
import org.nishat.util.log.Log;

public class Example {

    /**
     * protected group means from where event could not be unregistered.
     * @param args {@link String}[]
     */
    public static void main(String[] args) {
        //setting up events

        //create general event
        Event eventOf2 = new Event("eventOf2");
        //register general event, non protected group
        EventManager.getInstance().registerEvent("2", eventOf2);

        //create group
        EventGroup eventGroup3 = new EventGroup() {
            @Override
            public String name() {
                return "3";
            }
        };
        //create event
        Event eventOf3 = new Event("eventOf3");
        //register event with group
        EventManager.getInstance().registerEvent(eventGroup3, eventOf3);

        //create event
        Event eventOf5 = new Event("eventOf5");
        //register event with group
        EventManager.getInstance().registerEvent("5", eventOf5);
        //make group protected

        //add previous event to another group event
        Event eventOf7 = new Event("eventOf7");
        EventManager.getInstance().registerEvent("7", eventOf2);

        Event eventOf11 = new Event("eventOf11");
        EventManager.getInstance().registerEvent("11", eventOf11);

        Event eventOf13 = new Event("eventOf13");
        EventManager.getInstance().registerEvent("13", eventOf13);

        Thread caller = new Thread(new Caller());



        //notify Caller to continue execution after all listeners execution
        eventOf11.doAfterEachCall(event -> Log.i("Wait", "1 sec "+eventOf11.name()));

        //notify Caller to continue execution after all listeners execution
        eventOf11.doBeforeEachCall(event -> Log.i("Before", "from "+eventOf11.name()));

        //directly put Listener to event
        eventOf11.addListener(new Listener() {
            @Override
            public void exec(Object object) {
                EventParam eventParam = (EventParam) object;
                Log.i("Called", eventOf11.name()+ " "+eventParam.number +" is divisible by 11");
                Log.i("Listener", getId() + " in "+ eventOf11.name());
            }
        });

        //create Listener
        Listener listener3 = new Listener() {
            @Override
            public void exec(Object object) {
                EventParam eventParam = (EventParam) object;
                Log.i("Called", eventOf3.name()+ " "+eventParam.number +" is divisible by 3");
                Log.i("Listener", getId() + " in "+ eventOf3.name());
            }
        };
        //register to event
        EventManager.getInstance().getEvent("3", "eventOf3").addListener(listener3);

        //add another listener with id
        EventManager.getInstance().getEvent("3", "eventOf3").addListener(new Listener("listenerOfGroup3") {
            @Override
            public void exec(Object object) {
                EventParam eventParam = (EventParam) object;
                Log.i("Called", eventOf3.name()+ " "+eventParam.number +" is divisible by 3");
                Log.i("Listener", getId() + " in "+ eventOf3.name());

                //remove self after execution
                eventOf3.removeListener(this);
            }
        });
        caller.start();
    }
}
