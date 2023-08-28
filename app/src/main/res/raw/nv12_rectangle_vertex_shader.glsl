uniform mat4 mMatrix;
attribute vec3 vPos;
attribute vec2 vTex;
varying vec2 vTextureCoord;
void main() {
    gl_Position=mMatrix*vec4(vPos, 1.0);
    vTextureCoord = vTex;
}