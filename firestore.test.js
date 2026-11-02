const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

const PROJECT_ID = "demo-no-project";
let testEnv;

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules: fs.readFileSync("firestore.rules", "utf8"),
      host: "127.0.0.1",
      port: 8085
    }
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write potholes", async () => {
  const unauthedDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthedDb.collection("potholes").get());
  await assertFails(unauthedDb.collection("potholes").doc("p1").set({
    userId: "user123",
    street: "Grand Ave",
    depthInches: 3.2,
    severity: "CRITICAL",
    type: "CRATER",
    lane: "CENTER_LANE"
  }));
});

test("Authenticated user can report and read potholes", async () => {
  const authedDb = testEnv.authenticatedContext("driver_1").firestore();
  await assertSucceeds(authedDb.collection("potholes").doc("p1").set({
    userId: "driver_1",
    street: "Grand Ave",
    depthInches: 3.2,
    severity: "CRITICAL",
    type: "CRATER",
    lane: "CENTER_LANE"
  }));

  // Another authenticated user can view on radar
  const viewerDb = testEnv.authenticatedContext("driver_2").firestore();
  await assertSucceeds(viewerDb.collection("potholes").get());
  await assertSucceeds(viewerDb.collection("potholes").doc("p1").get());
});

test("User cannot update another driver's pothole report", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("potholes").doc("p1").set({
      userId: "driver_1",
      street: "Grand Ave",
      depthInches: 3.2,
      severity: "CRITICAL",
      type: "CRATER",
      lane: "CENTER_LANE"
    });
  });

  const attackerDb = testEnv.authenticatedContext("attacker").firestore();
  await assertFails(attackerDb.collection("potholes").doc("p1").update({
    depthInches: 1.0
  }));

  const ownerDb = testEnv.authenticatedContext("driver_1").firestore();
  await assertSucceeds(ownerDb.collection("potholes").doc("p1").update({
    depthInches: 3.8
  }));
});

test("DOT tickets are strictly scoped to the filing driver", async () => {
  const driverDb = testEnv.authenticatedContext("driver_1").firestore();
  await assertSucceeds(driverDb.collection("dot_tickets").doc("ticket_1").set({
    userId: "driver_1",
    potholeId: "p1",
    ticketNumber: "DOT-311-2026-99",
    department: "City DOT",
    status: "DISPATCHED"
  }));

  const otherDriverDb = testEnv.authenticatedContext("driver_2").firestore();
  await assertFails(otherDriverDb.collection("dot_tickets").doc("ticket_1").get());
});
