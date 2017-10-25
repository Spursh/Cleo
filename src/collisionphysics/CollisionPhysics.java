package collisionphysics;

/**
 * This class provides static methods for collision detection and responses,
 * based on Netwon's physics. It is modeled after java.lang.Math.
 * 
 * The collision detection is based on ray tracing and vector analysis. In all
 * situations, we try to compute the parameter t (collision time), and accept
 * the minimum t, such that, 0 < t <= detectionTimeLimit.
 * 
 * In a complex system (e.g., many bouncing balls), only the first collision
 * matters. Hence, we need to find the earliest (smallest) t among all the
 * detected collisions.
 *
 * @author	Spursh Ujjawal
 * @version 1.0
 * @since	2017-10-24 
 */
public class CollisionPhysics {

	// Working copy for computing response in intersect(DesktopScreen box),
	// to avoid repeatedly allocating objects.
	private static CollisionResponse tempResponse = new CollisionResponse();

	/**
	 * Detect collision for a moving point bouncing inside a rectangular container,
	 * within the given timeLimit. If collision is detected within the timeLimit,
	 * compute collision time and response in the given CollisionResponse object.
	 * Otherwise, set collision time to infinity. The result is passed back in the
	 * given CollisionResponse object.
	 * @param pointX : x-position of the center of the point.
	 * @param pointY : y-position of the center of the point.
	 * @param speedX : speed in x-direction.
	 * @param speedY : speed in y-direction.
	 * @param radius : radius of the point.
	 * @param rectX1 : top-left corner x of the rectangle
	 * @param rectY1 : top-left corner y of the rectangle
	 * @param rectX2 : bottom-right corner x of the rectangle
	 * @param rectY2 : bottom-right corner y of the rectangle
	 * @param timeLimit : max time to detect collision, in (0, 1] range.
	 * @param response : If collision is detected, update the collision time and response. Otherwise, set collision time to infinity.
	 */
	public static void pointIntersectsRectangleOuter(float pointX, float pointY, float speedX, float speedY,
			float radius, float rectX1, float rectY1, float rectX2, float rectY2, float timeLimit,
			CollisionResponse response) {

		// Assumptions:
		assert (rectX1 < rectX2) && (rectY1 < rectY2) : "Malformed rectangle!";
		assert (pointX >= rectX1 + radius) && (pointX <= rectX2 - radius) && (pointY >= rectY1 + radius)
				&& (pointY <= rectY2 - radius) : "Point (with radius) is outside the rectangular container!";
		assert (radius >= 0) : "Negative radius!";
		assert (timeLimit > 0) : "Non-positive time";

		response.reset(); // Reset detected collision time to infinity

		// A outer rectangular container box has 4 borders.
		// Need to look for the earliest collision, if any.

		// Right border
		pointIntersectsLineVertical(pointX, pointY, speedX, speedY, radius, rectX2, timeLimit, tempResponse);
		if (tempResponse.t < response.t) {
			response.copy(tempResponse); // Copy into resultant response
		}
		// Left border
		pointIntersectsLineVertical(pointX, pointY, speedX, speedY, radius, rectX1, timeLimit, tempResponse);
		if (tempResponse.t < response.t) {
			response.copy(tempResponse); // Copy into resultant response
		}
		// Top border
		pointIntersectsLineHorizontal(pointX, pointY, speedX, speedY, radius, rectY1, timeLimit, tempResponse);
		if (tempResponse.t < response.t) {
			response.copy(tempResponse); // Copy into resultant response
		}
		// Bottom border
		pointIntersectsLineHorizontal(pointX, pointY, speedX, speedY, radius, rectY2, timeLimit, tempResponse);
		if (tempResponse.t < response.t) {
			response.copy(tempResponse); // Copy into resultant response
		}
	}

	/**
	 * Detect collision for a moving point hitting a vertical line, within the given
	 * timeLimit. If collision is detected within the timeLimit, compute collision
	 * time and response in the given CollisionResponse object. Otherwise, set
	 * collision time to infinity. The result is passed back in the given CollisionResponse object.
	 * @param pointX : x-position of the center of the point.
	 * @param pointY : y-position of the center of the point.
	 * @param speedX : speed in x-direction.
	 * @param speedY : speed in y-direction.
	 * @param radius : radius of the point. Zero for a true point.
	 * @param lineX : x-value of the vertical line
	 * @param timeLimit : max time to detect collision, in (0, 1] range.
	 * @param response : If collision is detected, update the collision time and response. Otherwise, set collision time to infinity.
	 */
	public static void pointIntersectsLineVertical(float pointX, float pointY, float speedX, float speedY, float radius,
			float lineX, float timeLimit, CollisionResponse response) {

		// Assumptions:
		assert (radius >= 0) : "Negative radius!";
		assert (timeLimit > 0) : "Non-positive time";

		response.reset(); // Reset detected collision time to infinity

		// No collision possible if speedX is zero
		if (speedX == 0) { 
			return;
		}

		// Compute the distance to the line, offset by radius.
		float distance;
		if (lineX > pointX) {
			distance = lineX - pointX - radius;
		} else {
			distance = lineX - pointX + radius;
		}

		float t = distance / speedX;
		if (t > 0 && t <= timeLimit) {
			response.t = t;
			response.newSpeedX = -speedX; // Reflect horizontally
			response.newSpeedY = speedY; // No change vertically
		}
	}

	/**
	 * Detect collision for a moving point hitting a horizontal line, within the given
	 * timeLimit. If collision is detected within the timeLimit, compute collision
	 * time and response in the given CollisionResponse object. Otherwise, set
	 * collision time to infinity. The result is passed back in the given CollisionResponse object.
	 * @param pointX : x-position of the center of the point.
	 * @param pointY : y-position of the center of the point.
	 * @param speedX : speed in x-direction.
	 * @param speedY : speed in y-direction.
	 * @param radius : radius of the point. Zero for a true point.
	 * @param lineY : y-value of the horizontal line
	 * @param timeLimit : max time to detect collision, in (0, 1] range.
	 * @param response : If collision is detected, update the collision time and response. Otherwise, set collision time to infinity.
	 */
	public static void pointIntersectsLineHorizontal(float pointX, float pointY, float speedX, float speedY,
			float radius, float lineY, float timeLimit, CollisionResponse response) {

		// Assumptions:
		assert (radius >= 0) : "Negative radius!";
		assert (timeLimit > 0) : "Non-positive time";

		response.reset(); // Reset detected collision time to infinity

		// No collision possible if speedY is zero
		if (speedY == 0) {
			return;
		}

		// Compute the distance to the line, offset by radius.
		float distance;
		if (lineY > pointY) {
			distance = lineY - pointY - radius;
		} else {
			distance = lineY - pointY + radius;
		}

		float t = distance / speedY;
		if (t > 0 && t <= timeLimit) {
			response.t = t;
			response.newSpeedY = -speedY; // Reflect vertically
			response.newSpeedX = speedX; // No change horizontally
		}
	}

	/**
	 * Detect collision for a moving point hitting another moving point, within the
	 * given timeLimit. If collision is detected within the timeLimit, compute
	 * collision time and response in the given CollisionResponse object. Otherwise,
	 * set collision time to infinity. The result is passed back in the given
	 * CollisionResponse object.
	 * @param p1X : x-position of the center of point p1.
	 * @param p1Y : y-position of the center of point p1.
	 * @param p1SpeedX : p1's speed in x-direction.
	 * @param p1SpeedY : p1's speed in y-direction.
	 * @param p1Radius : p1's radius.
	 * @param p2X : x-position of the center of point p2.
	 * @param p2Y : y-position of the center of point p2.
	 * @param p2SpeedX : p2's speed in x-direction.
	 * @param p2SpeedY : p2's speed in y-direction.
	 * @param p2Radius : p2's radius. Zero for a true point.
	 * @param timeLimit : max time to detect collision, in (0, 1] range.
	 * @param p1Response : If collision is detected, update the collision time and response for p1. Otherwise, set collision time to infinity.
	 * @param p2Response : If collision is detected, update the collision time and response for p2. Otherwise, set collision time to infinity.
	 */
	public static void pointIntersectsMovingPoint(float p1X, float p1Y, float p1SpeedX, float p1SpeedY, float p1Radius,
			float p2X, float p2Y, float p2SpeedX, float p2SpeedY, float p2Radius, float timeLimit,
			CollisionResponse p1Response, CollisionResponse p2Response) {

		// Assumptions:
		assert (p1Radius >= 0) && (p2Radius >= 0) : "Negative radius!";
		assert timeLimit > 0 : "Non-positive time!";

		p1Response.reset(); // Set detected collision time to infinity
		p2Response.reset();

		// Call helper method to compute the collision time t.
		float t = pointIntersectsMovingPointDetection(p1X, p1Y, p1SpeedX, p1SpeedY, p1Radius, p2X, p2Y, p2SpeedX,
				p2SpeedY, p2Radius);

		if (t > 0 && t <= timeLimit) {
			// Call helper method to compute the responses in the 2 Response objects
			pointIntersectsMovingPointResponse(p1X, p1Y, p1SpeedX, p1SpeedY, p1Radius, p2X, p2Y, p2SpeedX, p2SpeedY,
					p2Radius, p1Response, p2Response, t);
		}
	}

	/**
	 * Helper method to detect the collision time (t) for two moving points.
	 * @param p1X : x-position of the center of point p1.
	 * @param p1Y : y-position of the center of point p1.
	 * @param p1SpeedX : p1's speed in x-direction.
	 * @param p1SpeedY : p1's speed in y-direction.
	 * @param p1Radius : p1's radius. 
	 * @param p2X : x-position of the center of point p2.
	 * @param p2Y : y-position of the center of point p2.
	 * @param p2SpeedX : p2's speed in x-direction.
	 * @param p2SpeedY : p2's speed in y-direction.
	 * @param p2Radius : p2's radius. Zero for a true point.
	 * @return smaller positive t, or infinity if collision is not possible.
	 */
	private static float pointIntersectsMovingPointDetection(float p1X, float p1Y, float p1SpeedX, float p1SpeedY,
			float p1Radius, float p2X, float p2Y, float p2SpeedX, float p2SpeedY, float p2Radius) {

		// Rearrange the parameters to set up the quadratic equation.
		double centerX = p1X - p2X;
		double centerY = p1Y - p2Y;
		double speedX = p1SpeedX - p2SpeedX;
		double speedY = p1SpeedY - p2SpeedY;
		double radius = p1Radius + p2Radius;
		double radiusSq = radius * radius;
		double speedXSq = speedX * speedX;
		double speedYSq = speedY * speedY;
		double speedSq = speedXSq + speedYSq;

		// Solve quadratic equation for collision time t
		double termB2minus4ac = radiusSq * speedSq
				- (centerX * speedY - centerY * speedX) * (centerX * speedY - centerY * speedX);
		if (termB2minus4ac < 0) {
			// No intersection.
			// Moving spheres may cross at different times, or move in parallel.
			return Float.MAX_VALUE;
		}

		double termMinusB = -speedX * centerX - speedY * centerY;
		double term2a = speedSq;
		double rootB2minus4ac = Math.sqrt(termB2minus4ac);
		double sol1 = (termMinusB + rootB2minus4ac) / term2a;
		double sol2 = (termMinusB - rootB2minus4ac) / term2a;
		// Accept the smallest positive t as the solution.
		if (sol1 > 0 && sol2 > 0) {
			return (float) Math.min(sol1, sol2);
		} else if (sol1 > 0) {
			return (float) sol1;
		} else if (sol2 > 0) {
			return (float) sol2;
		} else {
			// No positive t solution. Set detected collision time to infinity.
			return Float.MAX_VALUE;
		}
	}

	/**
	 * Helper method to compute the collision response given the collision time (t),
	 * for two moving points. Store and return the results in the two given CollisionResponse objects.
	 * @param p1X : x-position of the center of point p1.
	 * @param p1Y : y-position of the center of point p1.
	 * @param p1SpeedX : p1's speed in x-direction.
	 * @param p1SpeedY : p1's speed in y-direction.
	 * @param p1Radius : p1's radius.
	 * @param p2X : x-position of the center of point p2.
	 * @param p2Y : y-position of the center of point p2.
	 * @param p2SpeedX : p2's speed in x-direction.
	 * @param p2SpeedY : p2's speed in y-direction.
	 * @param p2Radius : p2's radius. Zero for a true point.
	 * @param p1Response : To update the collision time and response for p1. Reset time to infinity if error is detected.
	 * @param p2Response : To update the collision time and response for p2. Reset time to infinity if error is detected.
	 * @param t : the given detected collision time.
	 */
	private static void pointIntersectsMovingPointResponse(float p1X, float p1Y, float p1SpeedX, float p1SpeedY,
			float p1Radius, float p2X, float p2Y, float p2SpeedX, float p2SpeedY, float p2Radius,
			CollisionResponse p1Response, CollisionResponse p2Response, float t) {

		// Update the detected collision time in CollisionResponse.
		p1Response.t = t;
		p2Response.t = t;

		// Get the point of impact, to form the line of collision.
		double p1ImpactX = p1Response.getImpactX(p1X, p1SpeedX);
		double p1ImpactY = p1Response.getImpactY(p1Y, p1SpeedY);
		double p2ImpactX = p2Response.getImpactX(p2X, p2SpeedX);
		double p2ImpactY = p2Response.getImpactY(p2Y, p2SpeedY);

		// Direction along the line of collision is P, normal is N.
		// Get the direction along the line of collision
		double lineAngle = Math.atan2(p2ImpactY - p1ImpactY, p2ImpactX - p1ImpactX);

		// Project velocities from (x, y) to (p, n)
		double[] result = rotate(p1SpeedX, p1SpeedY, lineAngle);
		double p1SpeedP = result[0];
		double p1SpeedN = result[1];
		result = rotate(p2SpeedX, p2SpeedY, lineAngle);
		double p2SpeedP = result[0];
		double p2SpeedN = result[1];

		// Collision possible only if p1SpeedP - p2SpeedP > 0
		// Needed if the two balls overlap in their initial positions
		// Do not declare collision, so that they continue their course of movement until they are separated.
		if (p1SpeedP - p2SpeedP <= 0) {
			p1Response.reset(); // Set collision time to infinity
			p2Response.reset();
			return;
		}

		// Assume that mass is proportional to the cube of radius.
		// (All objects have the same density.)
		double p1Mass = p1Radius * p1Radius * p1Radius;
		double p2Mass = p2Radius * p2Radius * p2Radius;
		double diffMass = p1Mass - p2Mass;
		double sumMass = p1Mass + p2Mass;

		double p1SpeedPAfter, p1SpeedNAfter, p2SpeedPAfter, p2SpeedNAfter;
		// Along the collision direction P, apply conservation of energy and momentum
		p1SpeedPAfter = (diffMass * p1SpeedP + 2.0 * p2Mass * p2SpeedP) / sumMass;
		p2SpeedPAfter = (2.0 * p1Mass * p1SpeedP - diffMass * p2SpeedP) / sumMass;

		// No change in the perpendicular direction N
		p1SpeedNAfter = p1SpeedN;
		p2SpeedNAfter = p2SpeedN;

		// Project the velocities back from (p, n) to (x, y)
		result = rotate(p1SpeedPAfter, p1SpeedNAfter, -lineAngle);
		p1Response.newSpeedX = (float) result[0];
		p1Response.newSpeedY = (float) result[1];
		result = rotate(p2SpeedPAfter, p2SpeedNAfter, -lineAngle);
		p2Response.newSpeedX = (float) result[0];
		p2Response.newSpeedY = (float) result[1];
	}

	/**
	 * Helper method to rotation vector (x, y) by theta, in Graphics coordinates.
	 * y-axis is inverted. theta measured in counter-clockwise direction. Re-use the
	 * double[] rotateResult to avoid repeated new operations.
	 * @param x : x coordinate of the vector to be rotated.
	 * @param y : y coordinate of the vector to be rotated, inverted.
	 * @param theta : rotational angle in radians, counter-clockwise.
	 * @return An double array of 2 elements x and y, in the rotated coordinates.
	 */
	private static double[] rotateResult = new double[2];

	private static double[] rotate(double x, double y, double theta) {
		double sinTheta = Math.sin(theta);
		double cosTheta = Math.cos(theta);
		rotateResult[0] = x * cosTheta + y * sinTheta;
		rotateResult[1] = -x * sinTheta + y * cosTheta;
		return rotateResult;
	}

}
