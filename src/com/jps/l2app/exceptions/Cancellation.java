/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jps.l2app.exceptions;

/**
 * Not really for exception but for the cancellation command. This is used to trigger cancellation by getting out of the analyzeSubmission method.
 * 
 * @author org
 * org
 */
public class Cancellation extends Exception {

    public Cancellation(String message) {
        super(message);
    }
    
}
