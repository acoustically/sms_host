import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

public class SmsHost {
  public static String nullcheck(String str,  String Defaultvalue )
  {
    String ReturnDefault = "" ;
    if (str == null)
    {
      ReturnDefault =  Defaultvalue ;
    }
    else if (str == "" )
    {
      ReturnDefault =  Defaultvalue ;
    }
    else
    {
      ReturnDefault = str ;
    }
    return ReturnDefault ;
  }

  public static String base64Encode(String str) {
    byte[] strByte = str.getBytes();
    String result = Base64.getEncoder().encodeToString(strByte);
    return str;
  }

  public static void send(String sender, String receiver, String content) throws Exception {
    String charsetType = "EUC-KR";

    String sms_url = "";
    sms_url = "https://sslsms.cafe24.com/sms_sender.php"; // SMS 전송요청 URL
    String user_id = base64Encode("acoustically"); // SMS아이디
    String secure = base64Encode("bbdfdc39f85c5d0d132b437937a38254");//인증키
    String msg = base64Encode(nullcheck(content, "" ));
    String rphone = base64Encode(nullcheck(receiver, ""));
    String[] sphone = sender.split("-");
    String mode = base64Encode("1");
    String testflag = base64Encode(nullcheck("", ""));
    String smsType = base64Encode(nullcheck("S", ""));

    String[] host_info = sms_url.split("/");
    String host = host_info[2];
    String path = "/" + host_info[3];
    int port = 80;

    String arrKey[]
            = new String[] {"user_id","secure","msg", "rphone","sphone1","sphone2","sphone3"
            ,"mode","testflag","smsType",};
    String valKey[]= new String[arrKey.length] ;
    int count = 0;
    valKey[count++] = user_id;
    valKey[count++] = secure;
    valKey[count++] = msg;
    valKey[count++] = rphone;
    valKey[count++] = sphone[0];
    valKey[count++] = sphone[1];
    valKey[count++] = sphone[2];
    valKey[count++] = mode;
    valKey[count++] = testflag;
    valKey[count++] = smsType;

    String boundary = "";
    Random rnd = new Random();
    String rndKey = Integer.toString(rnd.nextInt(32000));
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] bytData = rndKey.getBytes();
    md.update(bytData);
    byte[] digest = md.digest();
    for(int i =0;i<digest.length;i++)
    {
      boundary = boundary + Integer.toHexString(digest[i] & 0xFF);
    }
    boundary = "---------------------"+boundary.substring(0,11);

    String data = "";
    String index = "";
    String value = "";
    for (int i=0;i<arrKey.length; i++)
    {
      index =  arrKey[i];
      value = valKey[i];
      data +="--"+boundary+"\r\n";
      data += "Content-Disposition: form-data; name=\""+index+"\"\r\n";
      data += "\r\n"+value+"\r\n";
      data +="--"+boundary+"\r\n";
    }

    Socket socket = new Socket(host, port);
    BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), charsetType));
    wr.write("POST "+path+" HTTP/1.0\r\n");
    wr.write("Content-Length: "+data.length()+"\r\n");
    wr.write("Content-type: multipart/form-data, boundary="+boundary+"\r\n");
    wr.write("\r\n");

    wr.write(data);
    wr.flush();

    BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream(),charsetType));
    String line;
    ArrayList tmpArr = new ArrayList();
    while ((line = rd.readLine()) != null) {
      tmpArr.add(line);
    }
    wr.close();
    rd.close();

    String tmpMsg = (String)tmpArr.get(tmpArr.size()-1);
    System.out.println(tmpMsg);
  }

  public static void main(String[] args) {
    try {
      send(args[0], args[1], args[2]);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
