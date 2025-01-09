#version 330

in vec2 f_texcoord;
in vec4 f_color;
in vec3 f_normal;
in vec3 f_light;

uniform sampler2D u_texture;
uniform vec3 u_lightDirection;

void main() {
    vec4 color = texture(u_texture, f_texcoord) * f_color;
    if(color.a <= 0)
        discard;
    color.rgb *= f_light.rgb;
    gl_FragColor = color;
}