#! /usr/bin/python

# from __future__ import print_function
import time,sys,re,math
try:
  import usb.core
except Exception as e:
  print("\t sudo zypper in python-usb \t\t# if you run SUSE")
  print("\t sudo apt-get python-usb   \t\t# if you run Ubuntu")
  raise e;

VENDOR_ID_ROLAND = 0x0b75
PRODUCT_ID_IMODELA = 0x0397
need_interface = False		# probably never needed, but harmful on some versions of usb.core
dev = usb.core.find(idVendor=VENDOR_ID_ROLAND, idProduct=PRODUCT_ID_IMODELA)
if dev is None:
  print "iModela not found. Power on, connect cable?"
  sys.exit(0)
dev_bus = dev.bus
dev_addr = dev.address
print "iModela found on usb bus=%d addr=%d" % (dev_bus, dev_addr)
log = sys.stdout
try:
  if dev.is_kernel_driver_active(0):
    if dev.detach_kernel_driver(0):
      print "detach_kernel_driver(0) returned nonzero"
except usb.core.USBError as e:
  print "usb.core.USBError:", e
  if e.errno == 13: print "Try running as root..."
  sys.exit(0)

for i in range(5):
  try:
    dev.reset();
    break
  except usb.core.USBError as e:
    print "reset failed: ", e
    print "retrying reset in 5 sec"
    time.sleep(5)

dev.set_configuration()
try:
  dev.set_interface_altsetting()      # Probably not really necessary.
except usb.core.USBError as e:
  print "set_interface_altsetting() failed: ", e


def usb_write(string, timeout=3000):
    """Send a command to the device. Long commands are sent in chunks of 4096 bytes.
       A nonblocking read() is attempted before write(), to find spurious diagnostics."""

    try:
      resp = usb_read(timeout=10) # poll the inbound buffer
      if resp:
        print "response before write('%s'): '%s'" % (string, resp)
    except:
      pass
    endpoint = 0x01
    chunksz = 1024
    #chunksz = 4096
    r = 0
    o = 0
    msg=''
    retry = 0
    while o < len(string):
      chunk = string[o:o+chunksz]
      try:
        if need_interface:
          r = dev.write(endpoint, string[o:o+chunksz], interface=0, timeout=timeout)
        else:
          r = dev.write(endpoint, string[o:o+chunksz], timeout=timeout)
      except TypeError as te:
        # write() got an unexpected keyword argument 'interface'
        raise TypeError("Write Exception: %s, %s dev=%s" % (type(te), te, type(s.dev)))
      except AttributeError as ae:
        # write() got an unexpected keyword argument 'interface'
        raise TypeError("Write Exception: %s, %s dev=%s" % (type(ae), ae, type(s.dev)))

      except Exception as e:
        # raise USBError(_str_error[ret], ret, _libusb_errno[ret])
        # usb.core.USBError: [Errno 110] Operation timed
        #print "Write Exception: %s, %s errno=%s" % (type(e), e, e.errno)
        import errno
        try:
          if e.errno == errno.ETIMEDOUT:
	    print "ETIMEDOUT, bytes %d" % (len(chunk))
            time.sleep(1)
            msg += 't'
            continue
        except Exception as ee:
          msg += "dev.write Error: " + ee
      else:
        if len(msg):
          print("msg="+msg)
          msg = ''

      # print("write([%d:%d], len=%d) = %d" % (o,o+chunksz, len(chunk), r), file=s.log)
      if r == 0 and retry < 50:
        time.sleep(1)
        retry += 1
	print "retry %d, bytes %d" % (retry, len(chunk))
        msg += 'r'
      elif r <= 0:
        raise ValueError('write %d bytes failed: r=%d' % (len(chunk), r))
      else:
        retry = 0
      o += r

    if o != len(string):
      raise ValueError('write all %d bytes failed: o=%d' % (len(string), o))

def usb_read(size=64, timeout=5000):
    """Low level read method"""
    endpoint = 0x82
    if need_interface:
      data = dev.read(endpoint, size, timeout=timeout, interface=0)
    else:
      data = dev.read(endpoint, size, timeout=timeout)
    if data is None:
      raise ValueError('read failed: none')
    return data.tostring()


xx=0
yy=5000
zz=2000

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
  usb_write(line)

