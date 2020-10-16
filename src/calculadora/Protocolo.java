package calculadora;

public class Protocolo {
    private double valor1;
    private double valor2;
    private String operador;
    private String valorString;

    Protocolo(String valor1, String valor2, String operador) {
        this.valorString    = valor1 + ":" + operador  + ":" + valor2;
        this.valor1         = Double.parseDouble(valor1);
        this.operador       = operador;
        this.valor2         = Double.parseDouble(valor2);
    }

    Protocolo() {

    }

    public double getValor1() {
        return valor1;
    }

    public void setValor1(double valor1) {
        this.valor1 = valor1;
    }

    public double getValor2() {
        return valor2;
    }

    public void setValor2(double valor2) {
        this.valor2 = valor2;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getValorString() {
        return valorString;
    }

    public void setValorString(String valorString) {
        this.valorString = valorString;
    }

    public void converterStringValue(String valor) {
        String[] valores = valor.split(":");
        if (valores.length == 3) {
            this.valor1   = Double.parseDouble(valores[0]);
            this.operador = valores[1];
            this.valor2   = Double.parseDouble(valores[2]);
            this.valorString = valor;
        }
    }
}
