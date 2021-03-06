s.boot;

(

x = SynthDef(\filtpulse, {arg note, gate;
var tone,mod,out,freq,max;
max = 0.00012;
freq = (note + LFNoise2.ar(3).range(0,1/12)).midicps;
mod = LocalIn.ar * SinOsc.ar(Rand(1,2).round(1) * 3.6).range(0,1.4);
tone = SinOsc.ar(freq, phase: mod) + VarSaw.ar(freq/2, mul: 2, width: XLine.ar(0.5,0.1,5)) + VarSaw.ar(freq*3, mul: XLine.ar(1,1/10000,2), width: XLine.ar(0.5,0.1,1));
LocalOut.ar(tone);
out = tone * (1/8);
out = MoogLadder.ar(out, Line.ar(freq,20000,0.15),0.1);
out = [DelayC.ar(out,max,LFNoise2.ar(12).range(0,max)), DelayC.ar(out,max,LFNoise2.ar(12).range(0,max))] * EnvGen.kr(Env.asr(0.01, 1, 0.04), gate, 1, doneAction: 2);

Out.ar(0, out * 0.7 * AmpComp.kr(note.midicps, 20.midicps));

}).store;

)

(

var notes, on, off;
MIDIIn.connect;
notes = Array.newClear(128);  // array has one slot per possible MIDI note

on = Routine({

var event, newNode;

loop {

event = MIDIIn.waitNoteOn;

newNode = Synth(\filtpulse);
newNode.set(\note, event.b);
newNode.set(\gate, 1);
notes.put(event.b, newNode);

}

}).play;


off = Routine({

var event;

loop {

event = MIDIIn.waitNoteOff;

notes[event.b].set(\gate, 0);

}

}).play;

q = { on.stop; off.stop; };

)

q.value;
