/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pint;

/**
 *
 * @author admin
 */
public class SynTaxErrorException extends Exception {

    public SynTaxErrorException() {
    }
    
    public SynTaxErrorException(String msg) {
        super(msg);
    }
    
}
