using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.Net;
using System.Net.Sockets;
using System.Text;

public class gv : MonoBehaviour
{
    // Start is called before the first frame update
    int size = 37;
    byte[] msg = { 123 };
    Socket clientSocket;
    void Start()
    {

        Socket serverSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
        serverSocket.Bind(new IPEndPoint(IPAddress.Any, 1234));
        serverSocket.Listen(1);
        clientSocket = serverSocket.Accept();
        byte[] data = new byte[size];
        int nb = clientSocket.Receive(data);
        for (int i = 0; i < nb; i++) { Debug.Log(data[i]); }

    }

    // Update is called once per frame
    void Update()
    {
        byte[] a = get();
        if (a[0] != 0)
        {
            Debug.Log(a[0] + "Error!!!!");
            return;
        }
        float[] b = tofloatarr(a);
        // string s = "";
        // for (int i = 0; i < 9; i++) { s = s + b[i] + " "; }

        Debug.Log(b[8]);
      //  finger("1", min(b[8], b[7]));
       // finger("2", b[8]);
      //  finger("3", b[7]);
      //  finger("4", b[6]);
        mainbone(b[0], b[1], b[2]);

    }

    float[] tofloatarr(byte[] a)
    {
        float[] f = new float[9];
        byte[] b = new byte[4];
        int i = 1;
        for (int j = 0; j < 9; j++)
        {
            for (int k = 0; k < 4; k++) { b[k] = a[i++]; }
            f[j] = System.BitConverter.ToSingle(b, 0);

        }
        return f;
    }

    byte[] get()
    {
        clientSocket.Send(msg);
        byte[] bytes = new byte[size];
        int nb = clientSocket.Receive(bytes);
        Debug.Log(nb);
        if (nb == 0) { return bytes; }
        if (nb != 37) { fill(nb, bytes); }
        return bytes;
    }

    void fill(int a, byte[] b)
    {
        byte[] bytes = new byte[size];
        Debug.Log("received partially. receiving remaining");
        int cnt = a;
        while (cnt < size)
        {
            int nb = clientSocket.Receive(bytes);
            for (int i = 0; i < nb; i++) { b[i + cnt] = bytes[i]; }
            cnt += nb;
        }
    }

    void finger(string i, float a)
    {
        a /= 100;
        float aa = 90 + 70 * a;
        float b = aa + 70 * a;
        float c = b + 70 * a;
        GameObject b1 = GameObject.FindGameObjectWithTag(i + "1");
        b1.transform.rotation = Quaternion.Euler(new Vector3(aa, 0, 0));
        GameObject b2 = GameObject.FindGameObjectWithTag(i + "2");
        b2.transform.rotation = Quaternion.Euler(new Vector3(b, 0, 0));
        GameObject b3 = GameObject.FindGameObjectWithTag(i + "3");
        b3.transform.rotation = Quaternion.Euler(new Vector3(c, 0, 0));
    }

    void mainbone(float a, float b, float c) {
        GameObject b1 = GameObject.FindGameObjectWithTag("main");
        b1.transform.rotation = Quaternion.Euler(new Vector3(a, b, c));
    }



    float min(float a, float b)
    {
        if (a < b) { return b; }
        return a;
    }




    //GameObject b1 = GameObject.FindGameObjectWithTag("ffg");
    //x(t, p); 




    public void x(GameObject t, Vector3 a)
    {
        t.transform.position = a;
    }


}
