# Proyecto Final – Estructura de Datos
## Sistema Interactivo de Exploración de Grafos con BFS y DFS

### Universidad Politécnica Salesiana
**Materia:** Estructura de Datos  
**Estudiante:**  
**Docente:**  
**Correo:** 

---

## Descripción del problema

El proyecto consiste en la construcción interactiva de un grafo sobre un mapa visual, donde el usuario puede crear nodos, conectar rutas dirigidas o no dirigidas y ejecutar algoritmos de búsqueda (BFS y DFS) para encontrar caminos entre dos puntos.

El sistema no solo muestra el resultado final, sino también el proceso de exploración paso a paso, permitiendo comparar visual y experimentalmente ambos algoritmos.

---

## Marco teórico

Un **grafo** es una estructura de datos formada por nodos (vértices) y conexiones (aristas).

Para recorrer grafos se utilizan algoritmos como:

### BFS (Breadth First Search)
- Usa una cola (Queue)
- Recorre por niveles
- **Garantiza el camino más corto**

### DFS (Depth First Search)
- Usa recursividad (pila implícita)
- Explora profundamente antes de retroceder
- **No garantiza el camino más corto**

---

## Tecnologías usadas

- Java
- Swing (interfaz gráfica)
- HashMap, List, Set, Queue
- MVC (Modelo Vista Controlador)
- Persistencia en archivos
- Registro de métricas en CSV

---

## Arquitectura MVC

| Capa | Función |
|-----|---------|
| model | Estructuras del grafo y algoritmos BFS/DFS |
| controller | Controla la ejecución de búsquedas |
| view | Interfaz gráfica y visualización |
| util | Persistencia y métricas |

---

## Estructura del proyecto

```
src/
 ├─ model/
 ├─ controller/
 ├─ view/
 ├─ util/
 └─ App.java
data/
 ├─ mapa.jpg
 ├─ config_grafo.txt
 └─ tiempos_ejecucion.csv
```

---

## Funcionamiento general

1. Se cargan nodos y conexiones desde archivo.
2. El usuario puede crear nodos y rutas con el mouse.
3. Se ejecuta BFS o DFS entre dos nodos.
4. Se visualiza la exploración y la ruta final.
5. Se registran los tiempos en un archivo CSV.

---

## Capturas de la interfaz

(Aquí debes poner 2 capturas de tu programa funcionando)

---

## Ejemplo de código BFS

```java
queue.add(start);
visited.add(start);

while (!queue.isEmpty()) {
    Node current = queue.poll();

    for (Node neighbor : adjList.get(current)) {
        if (!visited.contains(neighbor)) {
            visited.add(neighbor);
            parentMap.put(neighbor, current);
            queue.add(neighbor);
        }
    }
}
```

Este algoritmo garantiza encontrar el camino más corto.

---

## Comparación BFS vs DFS

| Característica | BFS | DFS |
|---------------|-----|-----|
| Estructura | Queue | Recursión |
| Camino más corto | Sí | No |
| Exploración | Por niveles | Profunda |

---

## Conclusiones 


---

## Aplicaciones futuras

- GPS y rutas óptimas
- Redes de comunicación
- Videojuegos
- Mapas inteligentes