var myReceiver = require('@amperka/ir-receiver').connect(P6);

myReceiver.on('receive', function(code, repeat) {
  if (repeat) {
    console.log('Key with code', code, 'is keeped pressed');
  } else {
    console.log('Key with code', code, 'was just pressed');
  }
});


var byte = 0x00100110;

var bit = 0;

var writeBit = function() {

  analogWrite(P2, 0.5, {freq: 38000});
  
  console.log((byte >> bit) & 0x01);

  if((byte >> bit) & 0x01) {
    setTimeout(function() {
      analogWrite(P2, 0, {freq: 38000});
    }, 1);
  } else {
    setTimeout(function() {
      analogWrite(P2, 0, {freq: 38000});
    }, 0.6);
  }

  setTimeout(function() {
    bit++;
    writeBit();
  }, 1.5);

};


setTimeout(function (){
  writeBit();
}, 3000);
