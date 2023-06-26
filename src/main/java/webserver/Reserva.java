package webserver;

public class Reserva {
    private int lugar;
    private String nome;
    private String ip;
    private String data;

    public Reserva(int lugar,String nome, String ip, String data) {
        this.lugar = lugar;
        this.nome = nome;
        this.ip = ip;
        this.data = data;
    }

    public Reserva(){}

    public int getLugar() {
        return lugar;
    }

    public void setLugar(int lugar) {
        this.lugar = lugar;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIp() { return ip; }

    public void setIp(String ip) { this.ip = ip; }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
