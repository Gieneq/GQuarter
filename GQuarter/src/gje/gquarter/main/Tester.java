package gje.gquarter.main;

import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Plane;
import gje.gquarter.toolbox.Triangle3Point;

import org.lwjgl.util.vector.Vector3f;

public class Tester {
	public static void main(String[] args) {
		Vector3f playerPos = new Vector3f(3f, 0f, 1f);
		Vector3f playerVel = new Vector3f(-1f, 0f, 0f);
		System.out.println("Player pos= " + playerPos);

		Vector3f pA = new Vector3f(0, 0, 0);
		Vector3f pB = new Vector3f(0, 2, 0);
		Vector3f pC = new Vector3f(0, 0, 2);
		Triangle3Point trian = new Triangle3Point(pA, pB, pC);
		System.out.println("tringle= " + trian);
		Plane plane = trian.getPlane();
		System.out.println("plane= " + plane);

		float t0 = (1 - (Vector3f.dot(plane.getNormal(), playerPos) + plane.getConst())) / (Vector3f.dot(plane.getNormal(), playerVel));
		float t1 = (-1 - (Vector3f.dot(plane.getNormal(), playerPos) + plane.getConst())) / (Vector3f.dot(plane.getNormal(), playerVel));

		System.out.println("t0= " + t0);
		System.out.println("t1= " + t1);

		// ///////////////////////////////////////////////////////////////////
		if (t0 > 1 || t1 < -1)
			System.out.println("Nie ma kolizji z PLASSZCZYZNA");
		else
			System.out.println("Jest kolizja z PLASSZCZYZNA");
		Vector3f newVel = new Vector3f(playerVel);
		newVel.scale(t0);
		Vector3f intersectionPoint0 = Vector3f.add(playerPos, newVel, null);
		// musimy odjac norm zeby uzyskac punkt styczny
		intersectionPoint0 = Vector3f.sub(intersectionPoint0, plane.getNormal(), null);
		System.out.println("intersectionPoint(Surface)= " + intersectionPoint0);
		System.out.println("intersectionTrianDistance= " + t0 * playerVel.length());

		boolean inside = trian.isPointInside(intersectionPoint0);
		System.out.println("intersection Point inside triangle= " + inside + "\n");

		// false to moze byc z wierzcholkiem lub krawedzia!
		if (!inside) {
			// sprawdzenie z wierzcholiem, nie wyklucza to krawedzi

			Vector3f[] verteks = new Vector3f[3];
			verteks[0] = trian.getpA();
			verteks[1] = trian.getpB();
			verteks[2] = trian.getpC();
			Float times[] = new Float[3]; // tu sa mozliwe wierzcholki, ich
											// czasu

			for (int i = 0; i < 3; ++i) {
				float a = Vector3f.dot(playerVel, playerVel);
				float b = 2 * (Vector3f.dot(playerVel, Vector3f.sub(playerPos, verteks[i], null)));
				float c = Vector3f.sub(verteks[i], playerPos, null).lengthSquared() - 1;

				Float rootV = Maths.getMinPositiveRoot(a, b, c);
				times[i] = rootV;
				if (rootV != null) {
					System.out.println("Znaleziono przeciecie z wierzcholkiem, ale moze byc tez krawedz, i=" + i);
					// w polowie krawedzi dlugosci 2 znajdzie 2 punkty
					System.out.println("intersectionPoint(Vertex)= " + intersectionPoint0);
					System.out.println("intersectionVertDistance= " + rootV * playerVel.length());

				}
			}

			// jak znajde przeciecie z krawedzia to je wezme, jak nie to wezme
			// wierzcholek jak ni ma wierzcholkow to trudno :p nie ma kolizji
			System.out.println();
			for (int i = 0; i < 3; ++i) {
				Vector3f edge = Vector3f.sub(verteks[(i + 1) % 3], verteks[i], null);
				Vector3f playerToVertex = Vector3f.sub(verteks[i], playerPos, null);

				float dot1 = Vector3f.dot(edge, playerVel);
				float dot2 = Vector3f.dot(edge, playerToVertex);

				float a = edge.lengthSquared() * (-playerVel.lengthSquared()) + dot1 * dot1;
				float b = edge.lengthSquared() * 2 * Vector3f.dot(playerVel, playerToVertex) - 2 * Vector3f.dot(edge, playerVel) * Vector3f.dot(edge, playerToVertex);
				float c = edge.lengthSquared() * (1 - playerToVertex.lengthSquared()) + dot2 * dot2;

				// jezeli mam rozwiazanie to sfera przecina jakis punkt na
				// nieskonczonej prostej, musze sprawdzic czy ten punkt jest
				// pomiedze wierzcholkami p1 i p2

				System.out.println("a= " + a + ", b= " + b + ", c= " + c);
				
				Float rootE = Maths.getMinPositiveRoot(a, b, c);
				if (rootE != null) {
					System.out.println("rootE= " + rootE);
					// mamy jakies rozwiazanie, parametryzujemy L(0) = p1, L(1)
					// = p2
					float f0 = (Vector3f.dot(edge, playerVel) * rootE - Vector3f.dot(edge, playerToVertex)) / edge.lengthSquared();
					if (f0 >= 0 && f0 <= 1) {
						// mamy przeciecie z prosta!!!!1
						// edge moge popsuc
						Vector3f tempPoint = new Vector3f(edge);
						tempPoint.scale(rootE);
						Vector3f intPointEdge = Vector3f.add(verteks[i], tempPoint, null);
						System.out.println("intersectionPoint(Edge)= " + intPointEdge);
						System.out.println("intersectionVertDistance= " + rootE * playerVel.length());

					}
				}

			}
		}
	}
}
