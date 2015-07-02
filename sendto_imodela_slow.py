#! /usr/bin/python

# from __future__ import print_function
import time,sys,re,math

usb_lp=open('/dev/usb/lp0', 'w')
#usb_lp=open('/dev/null', 'w')
xx=0
yy=5000
zz=2000

starting = False

zoff_mm=0.0
if len(sys.argv) > 2: zoff_mm=float(sys.argv[2])
print "zoff=",zoff_mm

n = 0
for line in open(sys.argv[1]).readlines():
  m = re.match(r'(.*Z(\d+),(\d+),)(\d+)(.*)$', line)
  if m:
    z = int(m.group(4))
    if z < 2000: z = int(zoff_mm*100+z) 
    line="%s%d%s" % (m.group(1), z, m.group(5))
    print line
  for i in range(10):
    try:
      usb_lp.write(line)
      usb_lp.flush()
      break
    except IOError as e:
      print "EIO", i, e
  v=4.0
  m = re.search(r'V([\d\.]+)', line)
  if m: v=float(m.group(1))
  m = re.search(r'Z(\d+),(\d+),(\d+)', line)
  if m:
    x=int(m.group(1))
    y=int(m.group(2))
    z=int(m.group(3))
    dx=abs(x-xx)
    dy=abs(y-yy)
    dz=abs(z-zz)
    xx=x
    yy=y
    zz=z
    l = math.sqrt(dx*dx+dy*dy+dz*dz) 
    if v > 3.0 and dz > math.sqrt(dx*dx+dy*dy): v=3.0
    t = l*0.01/v	# length unit: 1/100 mm; speed unit 100/sec
    print "%s run v=%g l=%g t=%g" % (line, v,l,t)
    time.sleep(t+1)
  elif re.search(r'RC', line):
    starting = True
  elif starting and re.search(r'MC0', line):
    time.sleep(20)
    starting = False
  else:
    time.sleep(1)


usb_lp.close()
