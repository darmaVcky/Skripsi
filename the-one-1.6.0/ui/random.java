/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.util.Random;

/**
 *
 * @author asus
 */
public class random {
    public static void main(String[] args) {
        Random r = new Random();
            int rand = r.nextInt();
//            System.out.println(rand);

        System.out.println(getRandomNumberInRange(5, 8));
    }
    
    
    private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
}
