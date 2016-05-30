/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

/**
 *
 * @author jps123
 */
public class NewClass {

    public static void main(String[] args) {
        int[] a = {-1, -3, -4, 5, 1, -6, 2, 1};
        System.out.println(solution(a));
    }

    public static int solution(int[] A) {
        if (A.length == 0) {
            return 0;
        }

        int total = 0;

        for (int i = 0; i < A.length; i++) {
            total += A[i];
        }

        int c = 0;
        for (int i = 0; i < A.length && i + 1 < A.length; i++) {
            c += A[i];

            if ((c - (total - A[i + 1])) == 0) {
                return i + 1;
            }

        }
        return -1;
    }

}
