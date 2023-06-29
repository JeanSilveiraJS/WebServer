package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RequestThread implements Runnable {
    private final int bufferSize = 1024;
    private final byte[] buffer = new byte[bufferSize];
    private int bufferUse;

    private final Socket ss;
    private final InputStream in;
    private final OutputStream out;

    private static Reserva reserva = new Reserva();
    private final Request req = new Request();

    public RequestThread(Socket ServerSocket) throws IOException {
        ss = ServerSocket;
        in = ss.getInputStream();
        out = ss.getOutputStream();

        //req.setThreadId(this.getName());
        req.setRequestIp(ss.getInetAddress().toString());

        //System.out.println("Main| ID: " + req.getThreadId());
    }

    @Override
    public void run() {
        bufferUse = ReadInputToBuffer(buffer);

        if (bufferUse < 0) {
            System.out.println("---\n");
            return;
        }

        req.setRequestString(new String(buffer, 0, bufferUse));
        File f = new File(getSourceDir());

        System.out.println(req.getThreadId() + "| Recurso solicitado: " + f);

        InputStream fileInputStream = searchFile(f);

        if (fileInputStream != null) {
            try {
                setOutputStream(fileInputStream);
                out.write(refactorOutput(req.getOutputStream()));

                System.out.println(req.getThreadId() + "| Recurso enviado");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            out.flush();
            out.close();
            ss.close();
            System.out.println(req.getThreadId() + "| Conexão fechada ---\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int ReadInputToBuffer(byte[] buffer) {
        try {
            return in.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getSourceDir() {
        if (req.getParams() != null) {
            if (req.getParam("lugar") != null) {
                int lugar = Integer.parseInt(req.getParam("lugar"));

                if (req.getResource().contains("consultar")){
                    reserva = Reservas.getReservaByLugar(lugar);

                    if (reserva != null) {
                        req.setResource("consultar.html");
                    } else {
                        req.setResource("index.html");
                    }
                } else if (req.getResource().contains("reservar")){
                    if (req.getParam("nome") != null) {
                        String nome = req.getParam("nome");

                        synchronized (this) {
                            if (!Reservas.verificaLugar(lugar)) {
                                Reserva r = new Reserva(lugar, nome, req.getRequestIp(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm:ss.SSS")));
                                reserva = r;
                                Reservas.reservas.add(r);
                                System.out.println(req.getThreadId() + "| Lugar " + lugar + " reservado com sucesso");

                                Log.write(reserva.getData() + " - " + nome + "@" + ss.getInetAddress() + " | Lugar: " + reserva.getLugar() + "\n");

                                req.setResource("sucesso.html");
                            } else {
                                System.out.println(req.getThreadId() + "| Lugar " + lugar + " já reservado");
                                req.setResource("falha.html");
                            }
                        }
                    }
                }
            }
        }

        if (req.getResource().equals("/")) {
            req.setResource("index.html");
        }

        req.setResource("html" + File.separatorChar + req.getResource().replace('/', File.separatorChar));

        return req.getResource();
    }

    private FileInputStream searchFile(File f) {
        if (f.exists()) {
            try {
                return new FileInputStream(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            byte[] notFound = "404 NOT FOUND\n\n".getBytes(StandardCharsets.UTF_8);

            try {
                out.write(notFound);
                System.out.println(req.getThreadId() + "| 404 NOT FOUND");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

    private int readFileToBuffer(InputStream file, byte[] buffer) {
        try {
            return file.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private byte[] refactorOutput(ByteArrayOutputStream byteArrayOutputFile) {

        String strOutputFile = byteArrayOutputFile.toString();

        if (req.getResource().contains("consultar")) {
            strOutputFile = strOutputFile.replace("${numLugar}", Integer.toString(reserva.getLugar()));
            strOutputFile = strOutputFile.replace("${nome}", reserva.getNome());
            strOutputFile = strOutputFile.replace("${data}", reserva.getData().substring(0, 18));
        } else if (req.getResource().contains("index")) {
            int numLugares = 50;
            String divInicio = "<form method='get' action='reservar' id='div-lugares' class='container grid text-center py-1 border gx-2'>";
            String divFim = "</form>";
            String div = divInicio + divFim;
            String botoes = divInicio;

            for (int i = 1; i <= numLugares; i++) {
                if (new Reservas().verificaLugar(i))
                    botoes = botoes.concat("<a href='consultar.html?lugar=" + i + "' class='btn btn-lg btn-danger col-1 mb-3 mx-3'>" + i + "</a>");
                else {
                    botoes = botoes.concat("<button type='submit' name='lugar' value='" + i + "' class='btn btn-lg btn-success col-1 mb-3 mx-3'>" + i + "</button>");
                }

            }

            botoes = botoes.concat(divFim);
            strOutputFile = strOutputFile.replace(div, botoes);
        }

        return strOutputFile.getBytes(StandardCharsets.UTF_8);
    }

    private void setOutputStream(InputStream fileIn) throws IOException {
        byte[] header = """
                HTTP/1.1 200 OK
                Content-Type: text/html; charset=utf-8
                
                """.getBytes(StandardCharsets.UTF_8);

        req.setOutputStream(new ByteArrayOutputStream());
        req.getOutputStream().write(header);

        bufferUse = readFileToBuffer(fileIn, buffer);

        do {
            req.getOutputStream().write(buffer, 0, bufferUse);
            bufferUse = readFileToBuffer(fileIn, buffer);
        } while (bufferUse > 0);
    }
}
