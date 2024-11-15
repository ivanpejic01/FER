names = ["Ana", "Petar", "Ana", "Lucija", "Vanja", "Pavao", "Lucija"]


def reverse_sort(names: list) -> list:
    temp_list = names.copy()
    for i in range(len(temp_list) - 1):
        for j in range(i, len(temp_list)):
            if (temp_list[i] < temp_list[j]):
                temp = temp_list[i]
                temp_list[i] = temp_list[j]
                temp_list[j] = temp

    return temp_list


print("names = ", names)

names_desc = reverse_sort(names)
print("names_desc = ", names_desc)
print("names after = ", names)
selected_names = names_desc[1:-1]
print("selected_names = ", selected_names)
unique_selected_names = set(selected_names)
print("unique_selected_names = ", unique_selected_names)
pass_names = []
for element in unique_selected_names:
    new_element = element + "-pass"
    pass_names.append(new_element)
print(pass_names)
