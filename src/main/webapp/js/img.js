 function searchImage(image1, image2, tmplw, tmplh) {
            var canvas = document.createElement('canvas'),
                ctx = canvas.getContext('2d'),
                            sw = image1.width,  // 原图宽度
                            sh = image1.height,  // 原图高度
                            tw = tmplw || 8,  // 模板宽度
                            th = tmplh || 8;  // 模板高度
            
            canvas.width = tw;
            canvas.height = th;
            
            ctx.drawImage(image1, 0, 0, sw, sh, 0, 0, tw, th);
            
      var pixels = ctx.getImageData(0, 0, tw, th);
            
            pixels = toGrayBinary(pixels, true, null, true);
            
            var canvas2 = document.createElement('canvas');
            var ctx2 = canvas2.getContext('2d');
            
            canvas2.width = tw;
            canvas2.height = th;
            
            ctx2.drawImage(image2, 0, 0, image2.width, image2.height, 0, 0, tw, th);
            
            var pixels2 = ctx2.getImageData(0, 0, tw, th);
                            
            pixels2 = toGrayBinary(pixels2, true, null, true);

            var similar = 0;

            for (var i = 0, len = tw * th; i < len; i++) {
                    if (pixels[i] == pixels2[i]) similar++;
            }
            
            similar = (similar / (tw * th)) * 100;
            
            return similar;
    }