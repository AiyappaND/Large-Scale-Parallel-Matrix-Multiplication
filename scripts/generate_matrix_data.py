import csv
import random



path_to_a = "a_matrix.csv"
path_to_b = "b_matrix.csv"

row_a = 2 
row_b = 2
col_a = 2
col_b = 2

with open(path_to_a, "w") as csv_file:
        writer = csv.writer(csv_file, delimiter=',')
        for row in range(row_a):
        	for col in range(col_a):
            		writer.writerow([row, col, random.randint(1, 100)])
            		
with open(path_to_b, "w") as csv_file:
        writer = csv.writer(csv_file, delimiter=',')
        for row in range(row_b):
        	for col in range(col_b):
            		writer.writerow([row, col, random.randint(1, 100)])

