const fs = require("fs");

if (process.argv.length !== 3) {
  console.log("Usage: node solve <input-file-name>");
} else {
  const fname = process.argv[2];
  const input = fs.readFileSync(fname, { encoding: "utf8" })
    .trim()
    .split("\n");

  let h;
  for (let i = 1; i < input.length; ++i) {
    if (input[i].startsWith("inp")) {
      h = i;
      break;
    }
  }

  const cm = input.length / h;

  for (let i = 0; i < h; ++i) {
    let s = "";
    for (let c = 0; c < cm; ++c) {
      s += input[i+c*h].padEnd(11, ' ');
    }
    console.log(s);
  }
}

function solvePart1(input) {
  const progs = splitProgramByInp(input);
  const cache = new Map();

  for (let i = 99999999999999; i >= 99999991111111; --i) {
    if (!isValidModelNum(i)) {
      continue;
    }

    let vars = { w: 0, x: 0, y: 0, z: 0 };
    for (j = 0; j < progs.length; ++j) {
      const hash = getHash(vars, j, i);
      if (cache.has(hash)) {
        vars = cache.get(hash);
        continue;
      }

      vars = simulate(progs[j], vars, i);
      cache.set(hash, vars);
    }
  }

  function getHash(vars, progidx, modelnum) {
    const n = modelnum.toString()[progidx];
    return `${vars["w"]},${vars["x"]},${vars["y"]},${vars["z"]},${progidx},${n}`;
  }

  function splitProgramByInp(input) {
    const idx = [];
    for (let i = 0; i < input.length; ++i) {
      const line = input[i];

      if (line.startsWith("inp")) {
        idx.push(i);
      }
    }
    idx.push(input.length);

    const chunks = [];
    for (let i = 0; i < idx.length-1; ++i) {
      chunks.push(input.slice(idx[i], idx[i+1]));
    }
    return chunks;
  }

  function isValidModelNum(num) {
    // return false if containing zero
    return (num.toString().indexOf("0") !== -1) ? false : true;
  }

  function simulate(input, vars, modelnum) {
    let ip = 0; // input pointer
    const nvars = Object.assign({}, vars);

    modelnum = modelnum.toString();
    for (let i = 0; i < input.length; ++i) {
      const line = input[i];

      const inst = readInstruction(line);
      const [arg1, arg1val, arg2, arg2val] = readArguments(line, nvars);

      switch (inst) {
      case "inp":
        const n = parseInt(modelnum[ip++]);
        nvars[arg1] = n;
        break;
      case "add":
        nvars[arg1] = arg1val + arg2val;
        break;
      case "mul":
        nvars[arg1] = arg1val * arg2val;
        break;
      case "div":
        nvars[arg1] = Math.floor(arg1val / arg2val);
        break;
      case "mod":
        nvars[arg1] = arg1val % arg2val;
        break;
      case "eql":
        nvars[arg1] = (arg1val === arg2val) ? 1 : 0;
        break;
      }
    }

    return vars;
  }

  function readInstruction(line) {
    return line.substring(0, 3);
  }

  function readArguments(line, vars) {
    const arg1 = line.substring(4, 5);
    const arg1val = vars[arg1];
    if (line.length === 5) {
      return [arg1, arg1val, null, null];
    }

    let arg2 = line.substring(6);
    let arg2val = (isNaN(arg2)) ? vars[arg2] : parseInt(arg2);

    return [arg1, arg1val, arg2, arg2val];
  }
}
