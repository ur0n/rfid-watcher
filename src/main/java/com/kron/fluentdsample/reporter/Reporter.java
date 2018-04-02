package com.kron.fluentdsample.reporter;

import com.kron.fluentdsample.observer.Observer;

import java.util.ArrayList;

public abstract class Reporter<T> {
    private ArrayList<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer){
        observers.add(observer);
    }

    public void updateObserver(Observer observer) {
        deleteObserver(observer);
        observers.add(observer);
    }

    private void deleteObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(){
       observers.forEach(observer -> {
           observer.update(this);
       });
    }

    public abstract T getValue();
    public abstract void execute();
}
