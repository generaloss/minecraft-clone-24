#version 330

in vec2 f_texcoord;
in vec4 f_color;
in vec3 f_normal;

uniform sampler2D u_texture;
uniform vec3 u_lightDirection;

void main() {
    vec4 color = texture(u_texture, f_texcoord) * f_color;
    if(color.a <= 0)
        discard;
    color.rgb *= dot(f_normal, u_lightDirection) * 0.1 + 0.9;
    gl_FragColor = color;
}
