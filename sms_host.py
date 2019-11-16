import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)
GPIO.setup(17, GPIO.IN)

can_send = True

while True:
  value = GPIO.input(17)
  if value > 0 and can_send:
    subprocess.run(["java", "SmsHost", "010-4101-9443", "010-4101-9443"])
    can_send = False
  else:
    can_send = True
