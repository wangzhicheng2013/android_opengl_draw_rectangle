precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D sTexture;
uniform sampler2D sUvTexture;
uniform int sYuv;
uniform int iRectNum;
uniform vec4 vRect[8];
void main() {
    if (1 == sYuv) {
        float y = texture2D(sTexture,vTextureCoord).r;
        float u = texture2D(sUvTexture,vTextureCoord).r - 0.5;
        float v = texture2D(sUvTexture,vTextureCoord).a - 0.5;
        float r = y + 1.4075*v;
        float g = y - 0.3455*u - 0.7169*v;
        float b = y + 1.779*u;
        r = clamp(r, 0.0, 1.0);
        g = clamp(g, 0.0, 1.0);
        b = clamp(b, 0.0, 1.0);
        vec4 color = vec4(r,g,b,1.0);
        int rect_count = 0;
        while (rect_count < iRectNum) {
            bvec2 gt = greaterThan(vTextureCoord, vRect[rect_count].xy);
            bvec2 lt = lessThan(vTextureCoord, vRect[rect_count].zw);
            if(gt.x && gt.y && lt.x && lt.y) {
                color = vec4(0.0, 0.0, 0.0, 1.0);
            }
            ++rect_count;
        }
        gl_FragColor = color;
    }
    else {
        gl_FragColor = texture2D(sTexture,vTextureCoord);
    }
}