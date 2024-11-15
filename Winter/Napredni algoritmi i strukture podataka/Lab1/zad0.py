def sortiraj(lista: list) -> list:
    for i in range(len(lista) - 1):
        for j in range(i, len(lista)):
            if (lista[i] > lista[j]):
                temp = lista[i]
                lista[i] = lista[j]
                lista[j] = temp
    return lista


lista = [5, 3, 4, 1, 6, 2]
nova = sortiraj(lista)
print("stara = ", lista)
print("\nnova = ", nova)
