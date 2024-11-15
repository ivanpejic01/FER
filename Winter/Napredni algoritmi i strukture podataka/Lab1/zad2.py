person_data = {
    "Ana": 1995,
    "Zoran": 1978,
    "Lucija": 2001,
    "Anja": 1997
}

for key in person_data:
    year = person_data[key]
    year = year - 1
    person_data[key] = year

year_age = []
for key in person_data:
    year = person_data[key]
    age = 2022 - year
    new = (year, age)
    year_age.append(new)

print(year_age)
