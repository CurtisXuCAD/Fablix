file = open('/var/lib/tomcat9/webapps/fablix/logTSTJ.txt', 'r')
Lines = file.readlines()
 
count = 0
ts = 0
tj = 0
# Strips the newline character
for line in Lines:
    count += 1
    ts += line.split(",")[0]
    tj += line.split(",")[1]

ts_avg = ts / float(count) / 1000000.0
tj_avg = tj / float(count) / 1000000.0

print(f"TS: {ts_avg}ms\n TJ: {tj_avg}ms")