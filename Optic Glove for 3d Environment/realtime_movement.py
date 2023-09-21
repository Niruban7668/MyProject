import socket
import struct
import time
import random
s=socket.socket()
s.connect(('127.0.0.1',1234))
cnt=[0,0,0]
cntt=[250,100,300]
s.send(chr(0).encode())
'''
while 1:
	print(s.recv(1),i)
	s.send(((chr(i)+"nirubanp")*4).encode())
	i=(i+1)%256
'''
def up(i):
	cnt[i]=(cnt[i]+1)%cntt[i]
	return cnt[i]/(cntt[i]/100)
#a=[9.8,8.5,1.2,5.6,7.9,5.2,4.3,3.9,6.8]
a=[0.0]*9


while 1:
#for i in range(10):
	#time.sleep(random.random())
	print(s.recv(1))
	a[-1]=up(0)
	a[-2]=up(1)
	a[-3]=up(2)
	ss=b'\x00'
	for i in a:		
		ba=bytearray(struct.pack('f',i))
		for j in ba: 
			h=hex(j)[2:]
			if len(h)==1: h="0"+h
			ss+=eval("b'\\x"+h+"'")
	#ss="A"*37
	sss=s.send(ss)
	print(sss,len(ss),ss)

