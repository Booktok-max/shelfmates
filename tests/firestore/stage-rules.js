/**
 * Copies the canonical firestore.rules (repo root) into the emulator project
 * directory.
 *
 * The Firestore emulator refuses a `rules` path that resolves outside its own
 * project directory ("../../firestore.rules is outside of project directory"),
 * so the file has to physically live next to firebase.json. Copying it here on
 * every run keeps a single source of truth at the repo root and makes it
 * impossible for the two to drift.
 *
 * Runs automatically via the `pretest` npm script.
 */

const { copyFileSync, existsSync } = require("fs");
const path = require("path");

const SOURCE = path.join(__dirname, "..", "..", "firestore.rules");
const DEST = path.join(__dirname, "firestore.rules");

if (!existsSync(SOURCE)) {
  console.error(
    `Cannot find canonical rules at ${SOURCE}\n` +
      "Expected firestore.rules in the repository root."
  );
  process.exit(1);
}

copyFileSync(SOURCE, DEST);
console.log(
  `Staged rules for emulator: ${SOURCE} -> ${path.relative(
    __dirname,
    DEST
  )}`
);