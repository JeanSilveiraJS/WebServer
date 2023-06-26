package webserver;

import java.util.ArrayList;

public class Reservas {
    public static ArrayList<Reserva> reservas = new ArrayList<>();

    public static boolean verificaLugar(int i) {
        for (Reserva r :
                reservas) {
            if (r.getLugar() == i) {
                return true;
            }
        }

        return false;
    }

    public static Reserva getReservaByLugar(int lugar) {
        for (Reserva r :
                reservas) {
            if (r.getLugar() == lugar) {
                return r;
            }
        }
        return null;
    }
}
