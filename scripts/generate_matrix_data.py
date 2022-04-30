import csv
import random



path_to_a = "a_matrix.csv"
path_to_b = "b_matrix.csv"

row_a = 1000
row_b = 1250
col_a = 1250
col_b = 1000

with open("input/" + path_to_a, "w") as csv_file1:
        writer = csv.writer(csv_file1, delimiter=',')
        for row1 in range(row_a):
            for col1 in range(col_a):
                writer.writerow([row1, col1, random.randint(1, 10)])
            csv_file1.flush()
            		
with open("input/" + path_to_b, "w") as csv_file2:
        writer = csv.writer(csv_file2, delimiter=',')
        for row2 in range(row_b):
            for col2 in range(col_b):
                writer.writerow([row2, col2, random.randint(1, 10)])
            csv_file2.flush()

