#version 330

in vec2 f_texcoord;
in vec4 f_color;

uniform sampler2D u_texture;

void main() {
    vec4 color = texture(u_texture, f_texcoord) * f_color;
    if(color.a <= 0)
        discard;
    gl_FragColor = color;
}