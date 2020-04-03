from random import randint

signList = ['+', '', '-']
operatorList = ['+', '-', '*']

inputList = list()
answerList = list()

def getResult(sign1, num1, operator, sign2, num2):
	if operator == '+':
		answerList.append((-1 * num1 if sign1 == '-' else num1) + (-1 * num2 if sign2 == '-' else num2))
	elif operator == "-":
		answerList.append((-1 * num1 if sign1 == '-' else num1) - (-1 * num2 if sign2 == '-' else num2))
	else:
		answerList.append((-1 * num1 if sign1 == '-' else num1) * (-1 * num2 if sign2 == '-' else num2))

for operator in operatorList:
	for sign1 in signList:
		for sign2 in signList:
			num = [randint(1, 1e+99),]
			num.append(randint(num[0] + 1, num[0] * 10))
			num.append(randint(num[0] * 10, 1e+100))

			for i in range(1, 3):
				inputList.append(sign1 + str(num[0]) + operator + sign2 + str(num[i]))
				getResult(sign1, num[0], operator, sign2, num[i])
				inputList.append(sign2 + str(num[i]) + operator + sign1 + str(num[0]))
				getResult(sign2, num[i], operator, sign1, num[0])
				inputList.append(sign1 + str(num[0]) + operator + sign2 + str(num[0]))
				getResult(sign1, num[0], operator, sign2, num[0])

				if sign1 == '':
					inputList.append('0' + operator + sign2 + str(num[0]))
					getResult('', 0, operator, sign2, num[0])
		
		inputList.append(sign1 + str(num[0]) + operator + '0')
		getResult(sign1, num[0], operator, '', 0)
	
	inputList.append('0' + operator + '0')
	getResult('', 0, operator, '', 0)

with open('input_list.txt', 'w') as f1:
	with open('answer_list.txt', 'w') as f2:
		for item in inputList:
			f1.write(item + '\n')
		for item in answerList:
			f2.write(str(item) + '\n')